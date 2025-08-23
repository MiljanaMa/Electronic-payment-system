import bcrypt
from fastapi import APIRouter, HTTPException
from schema import InitiatePaymentRequest
from service import create_paypal_order, capture_paypal_order
from model import Merchant, Transaction
from database import get_db, Base, engine
import uuid

Base.metadata.create_all(bind=engine)

router = APIRouter()


@router.post("/payments/initiate")
def initiate_payment(req: InitiatePaymentRequest):
    db = next(get_db())

    
    merchant = db.query(Merchant).filter(Merchant.merchant_id == req.merchant_id).first()
    if not merchant:
        raise HTTPException(status_code=404, detail="Merchant not found")
    
    #if not bcrypt.checkpw(req.merchant_pass.encode('utf-8'), merchant.merchant_password.encode('utf-8')):
    #    raise HTTPException(status_code=401, detail="Invalid merchant password")

    transaction_id = str(uuid.uuid4())
    transaction = Transaction(
        transaction_id=transaction_id,
        merchant_id=req.merchant_id,
        amount=req.amount,
        currency="EUR",
        status="INITIATED"
    )
    db.add(transaction)
    db.commit()
    db.refresh(transaction)
    
    try:
        approve_url = create_paypal_order(transaction, merchant)
    except Exception as e:
        transaction.status = "FAILED"
        db.commit()
        raise HTTPException(status_code=500, detail=f"PayPal order creation failed: {str(e)}")

    return {"transaction_id": transaction_id, "approve_url": approve_url}


@router.get("/paypal/return/{transaction_id}")
def paypal_return(transaction_id: str, token: str = None, PayerID: str = None):
    db = next(get_db())
    transaction = db.query(Transaction).filter(Transaction.transaction_id == transaction_id).first()
    if not transaction:
        raise HTTPException(status_code=404, detail="Transaction not found")
    capture_resp = capture_paypal_order(transaction)
    db.commit()
    return {"message": "Payment captured", "transaction_id": transaction_id, "paypal_response": capture_resp}


@router.get("/paypal/cancel/{transaction_id}")
def paypal_cancel(transaction_id: str):
    db = next(get_db())
    transaction = db.query(Transaction).filter(Transaction.transaction_id == transaction_id).first()
    if transaction:
        transaction.status = "FAILED"
        db.commit()
    return {"message": "Payment cancelled", "transaction_id": transaction_id}
