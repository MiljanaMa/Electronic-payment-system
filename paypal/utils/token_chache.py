import time

merchant_tokens = {}

def get_cached_token(merchant_id):
    token_data = merchant_tokens.get(merchant_id)
    if token_data and time.time() < token_data["expires_at"]:
        return token_data["access_token"]
    return None

def set_cached_token(merchant_id, access_token, expires_in):
    merchant_tokens[merchant_id] = {
        "access_token": access_token,
        "expires_at": time.time() + expires_in - 60
    }
