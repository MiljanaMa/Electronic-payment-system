from datetime import datetime
import bcrypt
from fastapi import APIRouter, HTTPException
from fastapi.responses import RedirectResponse
from schema import InitiatePaymentRequest, InitiateSubscriptionRequest
from service import cancel_payment_logic, check_subscription_status, create_paypal_order, capture_paypal_order, initiate_payment_logic, initiate_subscription_logic
from model import Merchant, Subscription, Transaction
from database import get_db, Base, engine
import uuid

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
def paypal_return(transaction_id: str, token: str = None):
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
    #treba dodati sta staviti u cookie
    response = RedirectResponse(url=transaction.success_url)
    response.set_cookie("MERCHANT_ORDER_ID", transaction.transaction_id)
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
    """
    Endpoint koji PayPal poziva kada korisnik potvrdi pretplatu.
    subscription_id -> PayPal subscription ID
    ba_token i token -> dodatni parametri od PayPala
    """
    db = next(get_db())
    print(subscription_id)
    # Pronađi subscription po PayPal ID
    subscription = db.query(Subscription).filter(Subscription.id == internal_id).first()
    if not subscription:
        raise HTTPException(status_code=404, detail="Subscription not found")

    #check_subscription_status(subscription_id)
    # Ažuriraj status
    subscription.status = "ACTIVE"  # ili neki drugi status koji koristiš za potvrđene pretplate
    subscription.paypal_subscription_id = subscription_id
    db.commit()
    db.refresh(subscription)

    # Opcionalno: možeš vratiti neki HTML ili redirect korisniku
    return {"message": "Subscription activated successfully", "subscription_id": subscription_id}