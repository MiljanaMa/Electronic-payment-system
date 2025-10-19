import uuid
import certifi
from fastapi import HTTPException
import requests
from config import PAYPAL_API_BASE, PAYPAL_CANCEL_URL, PAYPAL_SUBSCRIPTION_RETURN_URL
from database import get_db
from model import Merchant, Product, Subscription
from services.db_service import _get_merchant
from services.paypal import get_paypal_access_token


def initiate_subscription_logic(req):
    db = next(get_db())
    merchant = _get_merchant(db, req.merchant_id)
    subscription = Subscription(
        id=str(uuid.uuid4()),
        merchant_id=req.merchant_id,
        paypal_subscription_id = '',
        plan_id= '',
        status="INITIATED",
        success_url=req.success_url,
        error_url=req.error_url,
        failed_url=req.failed_url,
    )
    db.add(subscription)
    db.commit()
    db.refresh(subscription)

    try:
        approve_url = create_paypal_subscription(db, req, subscription, merchant)
    except HTTPException as e:
        subscription.status = "FAILED"
        db.commit()
        raise HTTPException(status_code=400, detail=f"Could not initiate PayPal subscription: {str(e)}")

    return {"subscription_id": subscription.paypal_subscription_id, "approve_url": approve_url}

def get_or_create_product(db, merchant, req):
    existing_product = db.query(Product).filter_by(product_id=req.product_id, merchant_id=merchant.merchant_id).first()
    if existing_product:
        return existing_product.paypal_product_id
    
    paypal_product_id = create_paypal_product(merchant, req.product_name, req.product_description)

    # Zapiši u bazu
    new_product = Product(
        name=req.product_name,
        description=req.product_description,
        merchant_id=merchant.id,
        paypal_product_id=paypal_product_id,
        product_id = req.product_id
    )
    db.add(new_product)
    db.commit()
    db.refresh(new_product)
    return paypal_product_id

def create_paypal_product(merchant, product_name, product_description):
    token = get_paypal_access_token(merchant)
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    data = {
        "name": product_name,
        "description": product_description,
        "type": "SERVICE",
        "category": "SOFTWARE"
    }

    r = requests.post(f"{PAYPAL_API_BASE}/v1/catalogs/products", json=data, headers=headers, verify=certifi.where())
    if r.status_code != 201:
        raise HTTPException(status_code=500, detail=f"Failed to create PayPal product: {r.text}")
    
    resp = r.json()
    return resp["id"]

def create_paypal_plan(db, req, subscription: Subscription, merchant: Merchant):
    token = get_paypal_access_token(merchant)
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    # Kreiramo product prvo
    product_id = get_or_create_product(db, merchant, req)

    data = {
        "product_id": product_id,
        "name": "Monthly Plan",
        "billing_cycles": [
            {
                "frequency": {
                    "interval_unit": "MONTH",
                    "interval_count": 1
                },
                "tenure_type": "REGULAR",
                "sequence": 1,
                "total_cycles": 0,
                "pricing_scheme": {
                    "fixed_price": {
                        "value": "10",
                        "currency_code": "USD"
                    }
                }
            }
        ],
        "payment_preferences": {
            "auto_bill_outstanding": True,
            "setup_fee": {"value": "0", "currency_code": "USD"},
            "setup_fee_failure_action": "CONTINUE",
            "payment_failure_threshold": 3
        }
    }

    r = requests.post(f"{PAYPAL_API_BASE}/v1/billing/plans", json=data, headers=headers, verify=certifi.where())
    if r.status_code != 201:
        raise HTTPException(status_code=500, detail=f"Failed to create PayPal plan: {r.text}")
    # trenutno necu cuvati u bazu
    resp = r.json()
    subscription.plan_id = resp["id"]  # P-xxxxx ID plana
    return subscription.plan_id

def create_paypal_subscription(db, req, subscription: Subscription, merchant: Merchant):
    plan_id = create_paypal_plan(db, req, subscription, merchant)
    token = get_paypal_access_token(merchant)
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }
    data = {
        "plan_id": plan_id,   # must be a PayPal plan_id already created in your account
        "application_context": {
            "brand_name": "My PSP Service",
            "locale": "en-US",
            "shipping_preference": "NO_SHIPPING",   # most digital subs don’t need shipping
            "user_action": "SUBSCRIBE_NOW",
            "return_url": f"{PAYPAL_SUBSCRIPTION_RETURN_URL}{subscription.id}",
            "cancel_url": f"{PAYPAL_CANCEL_URL}{subscription.id}",
        }
    }

    r = requests.post(f"{PAYPAL_API_BASE}/v1/billing/subscriptions", json=data, headers=headers, verify=certifi.where())
    if r.status_code != 201:
        raise HTTPException(status_code=500, detail=f"Failed to create PayPal subscription: {r.text}")

    resp = r.json()
    subscription.paypal_subscription_id = resp["id"]
    subscription.status = "APPROVAL_PENDING"
    
    db.add(subscription)
    db.commit()
    db.refresh(subscription)

    # Find approve link
    for link in resp.get("links", []):
        if link["rel"] == "approve":
            return link["href"]

    raise HTTPException(status_code=500, detail="Approve link not found in PayPal response")
'''
def capture_paypal_subscription(transaction: Transaction, merchant: Merchant, paypal_order_id: str):
    token = get_paypal_access_token(merchant)
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    r = requests.post(f"{PAYPAL_API_BASE}/v2/checkout/orders/{paypal_order_id}/capture", headers=headers, verify=certifi.where())
    if r.status_code != 201:
        transaction.status = "FAILED"
        raise HTTPException(status_code=500, detail=f"Failed to capture PayPal order: {r.text}")
    transaction.status = "CAPTURED"
    return r.json()
'''
def check_subscription_status(subscription_id):
    # Uzmi token za merchant-a
    db = next(get_db())
    merchant = _get_merchant(db, "123456")
    token = get_paypal_access_token(merchant)

    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }

    url = f"https://api-m.sandbox.paypal.com/v1/billing/subscriptions/{subscription_id}"
    response = requests.get(url, headers=headers, verify=certifi.where())

    if response.status_code != 200:
        raise Exception(f"Error fetching subscription: {response.text}")

    data = response.json()
    print(data)
    return {
        "status": data["status"],                # npr. ACTIVE, APPROVAL_PENDING, SUSPENDED
        "start_time": data.get("start_time"),
        "plan_id": data.get("plan_id"),
        "subscriber_email": data.get("subscriber", {}).get("email_address"),
        "last_payment": data.get("billing_info", {}).get("last_payment", {})
    }