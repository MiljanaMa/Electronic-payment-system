from sqlalchemy import Column, Integer, String, Float, DateTime
from database import Base
from datetime import datetime

class Transaction(Base):
    __tablename__ = "transactions"
    transaction_id = Column(String, primary_key=True, index=True)
    merchant_id = Column(String, index=True)
    amount = Column(Float)
    currency = Column(String)
    status = Column(String)  # INITIATED, APPROVAL_PENDING, CAPTURED, FAILED
    paypal_order_id = Column(String, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

class Merchant(Base):
    __tablename__ = "merchants"

    id = Column(Integer, primary_key=True, index=True)
    merchant_id = Column(String, unique=True, nullable=False)
    #merchant_password = Column(String, unique=True, nullable=False)
    paypal_client_id = Column(String, nullable=False)
    paypal_secret = Column(String, nullable=False)
    
    """
    def set_password(self, plain_password):
        self.merchant_password = bcrypt.hashpw(
            plain_password.encode('utf-8'), bcrypt.gensalt()
        ).decode('utf-8')

    def check_password(self, plain_password):
        return bcrypt.checkpw(
            plain_password.encode('utf-8'),
            self.merchant_password.encode('utf-8')
        )"""