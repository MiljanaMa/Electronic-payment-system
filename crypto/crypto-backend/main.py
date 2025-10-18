from fastapi import FastAPI, Request, HTTPException, Path, status
from pydantic import BaseModel
from typing import List
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import create_engine, Column, String, Float
from sqlalchemy.orm import declarative_base
from sqlalchemy.orm import sessionmaker
import uuid
from sqlalchemy.exc import IntegrityError

app = FastAPI()

# Allow frontend to talk to backend (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ---- SQLAlchemy setup ----
SQLALCHEMY_DATABASE_URL = "sqlite:///./crypto.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(bind=engine)
Base = declarative_base()

# ---- Database model ----
class TransactionDB(Base):
    __tablename__ = "transactions"
    payment_id = Column(String(50), primary_key=True, index=True)
    payment_url = Column(String(200), nullable=False)
    merchant_id = Column(String(50), nullable=False)
    amount = Column(Float, nullable=False)
    merchant_order_id = Column(String(50), nullable=False)
    success_url = Column(String(200), nullable=False)
    failed_url = Column(String(200), nullable=False)
    error_url = Column(String(200), nullable=False)
    transactionHash = Column(String, nullable=False)
    status = Column(String(50), default="Pending")

# ---- Database model ----
class MerchantDB(Base):
    __tablename__ = "merchants"
    merchant_id = Column(String(50), primary_key=True, index=True)
    merchantWallet = Column(String, nullable=False)

Base.metadata.create_all(bind=engine)

# ---- Pydantic model ----
class TransactionCreate(BaseModel):
    merchant_id: str
    amount: float
    merchant_order_id: str
    success_url: str
    failed_url: str
    error_url: str
    transactionHash: str

class TransactionResponse(BaseModel):
    payment_id: str
    payment_url: str
    merchant_id: str
    amount: float
    merchant_order_id: str
    success_url: str
    failed_url: str
    error_url: str
    transactionHash: str
    status: str

    class Config:
        orm_mode = True

class TransactionFrontResponse(BaseModel):
    id: str
    amount: float
    receiverWallet: str

    class Config:
        orm_mode = True

class TransactionUpdateRequest(BaseModel):
    transactionHash: str
    status: str = ""

class TransactionCompleteResponse(BaseModel):
    merchantOrderId: str
    statusURL: str

class MerchantResponse(BaseModel):
    merchant_id: str
    merchantWallet: str

    class Config:
        orm_mode = True

@app.on_event("startup")
def add_initial_merchant():
    db = SessionLocal()
    try:
        # Check if merchant already exists
        merchant = db.query(MerchantDB).filter(MerchantDB.merchant_id == "123456").first()
        if not merchant:
            new_merchant = MerchantDB(
                merchant_id="123456",
                merchantWallet="0x3939D057809Da4D31B018905806d3a0a21f1CAe4"
            )
            db.add(new_merchant)
            db.commit()
            print("Initial merchant added to database")
        else:
            print("Merchant already exists in database")
    except IntegrityError:
        db.rollback()
        print("Merchant insertion failed (possible duplicate)")
    finally:
        db.close()

# ---- Routes ----
@app.get("/")
def root():
    return {"message": "Crypto backend is running"}

@app.get("/merchants", response_model=List[MerchantResponse])
def get_merchants():
    db = SessionLocal()
    try:
        merchants = db.query(MerchantDB).all()
        return merchants
    finally:
        db.close()

@app.get("/transactions", response_model=List[TransactionResponse])
def get_transactions():
    db = SessionLocal()
    try:
        transactions = db.query(TransactionDB).all()
        return transactions
    finally:
        db.close()

@app.get("/transactions/{id}", response_model=TransactionFrontResponse)
def initialize_payment(id: str = Path(...)):
    db = SessionLocal()
    try:
        transaction = db.query(TransactionDB).filter(TransactionDB.payment_id == id).first()
        if not transaction:
            raise HTTPException(status_code=404, detail="Transaction not found")
        
        merchant = db.query(MerchantDB).filter(MerchantDB.merchant_id == transaction.merchant_id).first()
        if not merchant:
            raise HTTPException(status_code=404, detail="Merchant not found")

        return TransactionFrontResponse(
            id=transaction.payment_id,
            amount=transaction.amount,
            receiverWallet=merchant.merchantWallet
        )
    finally:
        db.close()

@app.put("/transactions/{id}/complete", response_model=TransactionCompleteResponse, status_code=status.HTTP_200_OK)
def complete_transaction(id: str, update: TransactionUpdateRequest):
    db = SessionLocal()
    try:
        transaction = db.query(TransactionDB).filter(TransactionDB.payment_id == id).first()
        if not transaction:
            raise HTTPException(status_code=404, detail="Transaction not found")

        transaction.transactionHash = update.transactionHash
        transaction.status = update.status
        db.commit()
        db.refresh(transaction)

        return TransactionCompleteResponse(
            merchantOrderId=transaction.merchant_order_id,
            statusURL=transaction.success_url,
        )
    finally:
        db.close()

@app.post("/crypto_payment")
async def create_crypto_payment(request: Request):
    print("Crypto service - uslo")
    data = await request.json()
    required_fields = [
        "MERCHANT_ID",
        "AMOUNT",
        "MERCHANT_ORDER_ID",
        "SUCCESS_URL",
        "FAILED_URL",
        "ERROR_URL",
    ]

    if not all(field in data for field in required_fields):
        raise HTTPException(status_code=400, detail="Missing required fields")

    db = SessionLocal()
    try:
        payment_id = f"{uuid.uuid4().hex[:6]}"
        payment_url = f"http://localhost:4200/{payment_id}"

        transaction = TransactionDB(
            merchant_id=data["MERCHANT_ID"],
            amount=float(data["AMOUNT"]),
            merchant_order_id=data["MERCHANT_ORDER_ID"],
            success_url=data["SUCCESS_URL"],
            failed_url=data["FAILED_URL"],
            error_url=data["ERROR_URL"],
            transactionHash="",
            payment_id=payment_id,
            payment_url=payment_url,
            status="Pending",
        )

        db.add(transaction)
        db.commit()

        return {
            "PAYMENT_ID": payment_id,
            "PAYMENT_URL": payment_url,
        }
    finally:
        db.close()

# ---- Run backend ----
if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8070)
