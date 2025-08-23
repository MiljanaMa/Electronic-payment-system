import requests
import time
from database import get_db
from model import Merchant, Transaction
from fastapi import HTTPException

PAYPAL_CLIENT_ID = "AWxmxxO_s2I9BserUKGNEkqTFsNYhXWLVIHx2xk4NYUDUaPFHbVdlOe_HkPxwmVPryHPgVHOKndLjI8h"
PAYPAL_SECRET = "ELOowKx_lT_Agey_usSDLSRZ_wJUvwM6pHE6M9jzrwvqCCxndOhuHlg_Y7IL28torzAOMhv-gdzhjsvo"
PAYPAL_API_BASE = "https://api-m.sandbox.paypal.com"

merchant_tokens = {} 

def get_paypal_access_token(merchant):
    merchant_id = merchant.merchant_id

    if merchant_id in merchant_tokens:
        token_data = merchant_tokens[merchant_id]
        if time.time() < token_data["expires_at"]:
            return token_data["access_token"]

    auth = (merchant.paypal_client_id, merchant.paypal_secret)
    headers = {"Accept": "application/json", "Accept-Language": "en_US"}
    data = {"grant_type": "client_credentials"}

    r = requests.post(f"{PAYPAL_API_BASE}/v1/oauth2/token", headers=headers, data=data, auth=auth)
    if r.status_code != 200:
        raise HTTPException(status_code=500, detail=f"Failed to get PayPal access token: {r.text}")

    resp = r.json()
    access_token = resp["access_token"]
    expires_at = time.time() + resp["expires_in"] - 60

    merchant_tokens[merchant_id] = {
        "access_token": access_token,
        "expires_at": expires_at
    }

    return access_token


def create_paypal_order(transaction: Transaction, merchant: Merchant):
   
    token = get_paypal_access_token(merchant)
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }
    data = {
        "intent": "CAPTURE",
        "purchase_units": [{
            "amount": {"currency_code": transaction.currency, "value": str(transaction.amount)}
        }],
        "application_context": {
            "return_url": f"http://localhost:8000/paypal/return/{transaction.transaction_id}",
            "cancel_url": f"http://localhost:8000/paypal/cancel/{transaction.transaction_id}"
        }
    }

    r = requests.post(f"{PAYPAL_API_BASE}/v2/checkout/orders", json=data, headers=headers)
    if r.status_code != 201:
        raise HTTPException(status_code=500, detail=f"Failed to create PayPal order: {r.text}")

    resp = r.json()
    transaction.paypal_order_id = resp["id"]
    transaction.status = "APPROVAL_PENDING"

    # Nadji approve link
    for link in resp["links"]:
        if link["rel"] == "approve":
            return link["href"]

    raise HTTPException(status_code=500, detail="Approve link not found in PayPal response")


def capture_paypal_order(transaction: Transaction):
    token = get_paypal_access_token()
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    r = requests.post(f"{PAYPAL_API_BASE}/v2/checkout/orders/{transaction.paypal_order_id}/capture", headers=headers)
    if r.status_code != 201:
        transaction.status = "FAILED"
        raise HTTPException(status_code=500, detail=f"Failed to capture PayPal order: {r.text}")
    transaction.status = "CAPTURED"
    return r.json()
