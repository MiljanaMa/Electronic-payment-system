from fastapi import HTTPException
from database import get_db
from model import Merchant, Transaction, Subscription

def _get_merchant(db, merchant_id: str) -> Merchant:
    merchant = db.query(Merchant).filter(Merchant.merchant_id == merchant_id).first()
    if not merchant:
        raise HTTPException(status_code=404, detail="Merchant not found")
    return merchant

def _get_transaction(db, transaction_id: str) -> Transaction:
    transaction = db.query(Transaction).filter(Transaction.transaction_id == transaction_id).first()
    if not transaction:
        raise HTTPException(status_code=404, detail="Transaction not found")
    return transaction

def save_transaction(db, transaction: Transaction):
    db.add(transaction)
    db.commit()
    db.refresh(transaction)

def save_subscription(db, subscription: Subscription):
    db.add(subscription)
    db.commit()
    db.refresh(subscription)

def update_subscription_status(db, subscription: Subscription, status: str, paypal_subscription_id: str):
    subscription.status = status
    subscription.paypal_subscription_id = paypal_subscription_id
    db.commit()
    db.refresh(subscription)
    return subscription
