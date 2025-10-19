from datetime import datetime
import uuid
import certifi
from fastapi import HTTPException
from fastapi.responses import RedirectResponse
import requests
from config import PAYPAL_API_BASE, PAYPAL_CANCEL_URL, PAYPAL_RETURN_URL
from database import get_db
from model import Merchant, Transaction
from services.db_service import _get_merchant, _get_transaction
from services.paypal import get_paypal_access_token

def initiate_payment_logic(req):
    db = next(get_db())
    merchant = _get_merchant(db, req.merchant_id)

    transaction = Transaction(
        transaction_id=str(uuid.uuid4()),
        merchant_id=req.merchant_id,
        merchant_order_id = req.merchant_order_id,
        amount=req.amount,
        currency="EUR",
        status="INITIATED",
        success_url=req.success_url,
        error_url=req.error_url,
        failed_url=req.failed_url,
    )
    db.add(transaction)
    db.commit()
    db.refresh(transaction)

    try:
        approve_url = create_paypal_order(transaction, merchant)
    except HTTPException as e:
        transaction.status = "FAILED"
        db.commit()
        raise HTTPException(status_code=400, detail=f"Could not initiate PayPal order: {str(e)}")

    return {"transaction_id": transaction.transaction_id, "approve_url": approve_url}

def create_paypal_order(transaction: Transaction, merchant: Merchant):
   
    token = get_paypal_access_token(merchant)
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }
    data = {
        "intent": "CAPTURE",
        "purchase_units": [{
            "amount": {"currency_code": transaction.currency, "value": str(transaction.amount)}
        }],
        "application_context": {
            "return_url": f"{PAYPAL_RETURN_URL}{transaction.transaction_id}",
            "cancel_url": f"{PAYPAL_CANCEL_URL}{transaction.transaction_id}",
        }
    }

    r = requests.post(f"{PAYPAL_API_BASE}/v2/checkout/orders", json=data, headers=headers, verify=certifi.where())
    if r.status_code != 201:
        raise HTTPException(status_code=500, detail=f"Failed to create PayPal order: {r.text}")

    resp = r.json()
    transaction.paypal_order_id = resp["id"]
    transaction.status = "APPROVAL_PENDING"

    # Nadji approve link
    for link in resp["links"]:
        if link["rel"] == "approve":
            return link["href"]

    raise HTTPException(status_code=500, detail="Approve link not found in PayPal response")

def capture_payment_logic(transaction_id: str, token: str, db):
    transaction = _get_transaction(db, transaction_id)
    merchant = _get_merchant(db, transaction.merchant_id)

    capture_resp = capture_paypal_order(transaction, merchant, token)
    db.commit()

    response = RedirectResponse(url=transaction.success_url)
    response.set_cookie("MERCHANT_ORDER_ID", transaction.transaction_id)
    response.set_cookie("ACQUIRER_ORDER_ID", capture_resp.get("id", ""))
    response.set_cookie(
        "PAYMENT_ID",
        capture_resp.get("purchase_units", [{}])[0].get("payments", {}).get("captures", [{}])[0].get("id", ""),
    )
    response.set_cookie("ACQUIRER_TIMESTAMP", datetime.now().isoformat())
    response.set_cookie("status", "SUCCESS")
    return response
def capture_paypal_order(transaction: Transaction, merchant: Merchant, paypal_order_id: str):
    token = get_paypal_access_token(merchant)
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    r = requests.post(f"{PAYPAL_API_BASE}/v2/checkout/orders/{paypal_order_id}/capture", headers=headers, verify=certifi.where())
    if r.status_code != 201:
        transaction.status = "FAILED"
        raise HTTPException(status_code=500, detail=f"Failed to capture PayPal order: {r.text}")
    transaction.status = "CAPTURED"
    return r.json()

def cancel_payment_logic(transaction_id: str):
    db = next(get_db())
    transaction = _get_transaction(db, transaction_id)
    transaction.status = "FAILED"
    db.commit()
    return {"message": "Payment cancelled", "transaction_id": transaction.transaction_id}
