import uuid
from sqlalchemy import JSON, Column, ForeignKey, Integer, String, Float, DateTime
from database import Base
from datetime import datetime
from sqlalchemy.orm import relationship

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
    success_url = Column(String, nullable=False)
    failed_url = Column(String, nullable=False)
    error_url = Column(String, nullable=False)

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

class Subscription(Base):
    __tablename__ = "subscriptions"

    id = Column(String, primary_key=True, index=True, default=lambda: str(uuid.uuid4()))
    merchant_id = Column(String, index=True)
    paypal_subscription_id = Column(String)
    plan_id = Column(String, index=True)
    status = Column(String, default="INITIATED")
    success_url = Column(String)
    error_url = Column(String)
    failed_url = Column(String)
    
class Product(Base):
    __tablename__ = "products"

    id = Column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    name = Column(String, nullable=False)
    description = Column(String, nullable=True)
    type = Column(String, default="SERVICE")  # PayPal: SERVICE
    category = Column(String, default="SOFTWARE")  # PayPal: SOFTWARE
    paypal_product_id = Column(String, unique=True, nullable=False)  # ID iz PayPala
    product_id = Column(String, unique=False, nullable=False)
    merchant_id = Column(String, index=True)

    # Veza ka planovima
    plans = relationship("Plan", back_populates="product")
    
class Plan(Base):
    __tablename__ = "plans"

    id = Column(String, primary_key=True, default=lambda: str(uuid.uuid4()))
    product_id = Column(String, ForeignKey("products.id"), nullable=False)
    name = Column(String, nullable=False)
    billing_cycles = Column(JSON, nullable=False)
    payment_preferences = Column(JSON, nullable=False)
    paypal_plan_id = Column(String, unique=True, nullable=False)  # ID iz PayPala

    # Veza ka proizvodu
    product = relationship("Product", back_populates="plans")