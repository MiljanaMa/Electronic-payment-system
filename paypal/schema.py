from pydantic import BaseModel

class InitiatePaymentRequest(BaseModel):
    merchant_id: str
    merchant_password: str
    amount: float
    merchant_order_id: str
    success_url: str
    failed_url: str
    error_url: str