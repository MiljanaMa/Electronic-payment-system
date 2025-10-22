import time
import requests
import certifi
from fastapi import HTTPException
from config import PAYPAL_API_BASE
from model import Merchant

merchant_tokens = {}

def get_paypal_access_token(merchant: Merchant) -> str:
    if merchant.merchant_id in merchant_tokens:
        token_data = merchant_tokens[merchant.merchant_id]
        if time.time() < token_data["expires_at"]:
            return token_data["access_token"]

    auth = (merchant.paypal_client_id, merchant.paypal_secret)
    headers = {"Accept": "application/json", "Accept-Language": "en_US"}
    data = {"grant_type": "client_credentials"}

    r = requests.post(f"{PAYPAL_API_BASE}/v1/oauth2/token", headers=headers, data=data, auth=auth, verify=certifi.where())
    print(merchant.paypal_client_id)
    print(merchant.paypal_secret)
    if r.status_code != 200:
        raise HTTPException(status_code=500, detail=f"Failed to get PayPal access token: {r.text}")

    resp = r.json()
    access_token = resp["access_token"]
    expires_at = time.time() + resp["expires_in"] - 60
    merchant_tokens[merchant.merchant_id] = {"access_token": access_token, "expires_at": expires_at}
    return access_token
