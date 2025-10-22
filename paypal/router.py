from datetime import datetime
import bcrypt
from fastapi import APIRouter
from fastapi.responses import RedirectResponse
from schema import InitiatePaymentRequest, InitiateSubscriptionRequest
from model import Merchant, Transaction
from database import get_db, Base, engine
from services.db_service import update_subscription_status
from services.payment_service import cancel_payment_logic, capture_paypal_order, initiate_payment_logic
from services.subscription_service import initiate_subscription_logic

Base.metadata.create_all(bind=engine)

router = APIRouter()

@router.post("/payments/initiate")
def initiate_payment(req: InitiatePaymentRequest):
    #if not bcrypt.checkpw(req.merchant_pass.encode('utf-8'), merchant.merchant_password.encode('utf-8')):
    #    raise HTTPException(status_code=401, detail="Invalid merchant password")
    result = initiate_payment_logic(req)
    return result

@router.post("/subscriptions/initiate")
def initiate_subscription(req: InitiateSubscriptionRequest):
    print(req)
    result = initiate_subscription_logic(req)
    return result

@router.get("/paypal/return/{transaction_id}")
def paypal_return(transaction_id: str, token: str = None, PayerID: str = None):
    print(transaction_id)
    db = next(get_db())
    transaction = db.query(Transaction).filter(Transaction.transaction_id == transaction_id).first()
    if not transaction:
        return RedirectResponse(transaction.error_url)
    merchant = db.query(Merchant).filter(Merchant.merchant_id == transaction.merchant_id).first()
    if not merchant:
        return RedirectResponse(transaction.error_url)
    capture_resp = capture_paypal_order(transaction, merchant, token)
    db.commit()
    print(transaction.success_url)
    #treba dodati sta staviti u cookie
    response = RedirectResponse(url=transaction.success_url)
    response.set_cookie("MERCHANT_ORDER_ID", transaction.merchant_order_id)
    response.set_cookie("ACQUIRER_ORDER_ID", capture_resp.get("id", ""))
    response.set_cookie("PAYMENT_ID", capture_resp.get("purchase_units", [{}])[0].get("payments", {}).get("captures", [{}])[0].get("id", ""))
    response.set_cookie("ACQUIRER_TIMESTAMP", datetime.now().isoformat())
    response.set_cookie("status", "SUCCESS")

    return response


@router.get("/paypal/cancel/{transaction_id}")
def paypal_cancel(transaction_id: str):
    return cancel_payment_logic(transaction_id)

@router.get("/paypal/return/subscription/{internal_id}")
def paypal_return(internal_id: str, subscription_id: str, ba_token: str, token: str):
    print(internal_id)
    subscription = update_subscription_status(
        internal_id=internal_id,
        paypal_subscription_id=subscription_id,
        status="ACTIVE"
    )
    response = RedirectResponse(url=subscription.success_url)
    print(subscription.merchant_subscription_id)
    response.set_cookie("MERCHANT_ORDER_ID", subscription.merchant_subscription_id)
    response.set_cookie("ACQUIRER_ORDER_ID", subscription.paypal_subscription_id)
    response.set_cookie("PAYMENT_ID",  subscription.id)
    response.set_cookie("ACQUIRER_TIMESTAMP", datetime.now().isoformat())
    response.set_cookie("status", "SUCCESS")

    return response