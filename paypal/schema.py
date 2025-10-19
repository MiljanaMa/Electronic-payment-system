from pydantic import BaseModel

class InitiatePaymentRequest(BaseModel):
    merchant_id: str
    merchant_password: str
    amount: float
    merchant_order_id: str
    success_url: str
    failed_url: str
    error_url: str
    
class InitiateSubscriptionRequest(BaseModel):
    merchant_id: str
    merchant_password: str
    merchant_subscription_id: str
    product_id: str
    product_name: str
    product_description: str
    success_url: str
    error_url: str
    failed_url: str