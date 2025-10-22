import ssl
from fastapi import FastAPI
import uvicorn
from router import router

app = FastAPI(title="PayPal Service")
app.include_router(router)

if __name__ == "__main__":

    # Direktno prosleđivanje cert i key fajlova
    config = uvicorn.Config(
        app=app,
        host="0.0.0.0",
        port=8087,
        reload=True,
        ssl_certfile="C:/Users/Miljana/Documents/SEP/Electronic-payment-system/paypal/certs/localhost+2.crt",
        ssl_keyfile="C:/Users/Miljana/Documents/SEP/Electronic-payment-system/paypal/certs/localhost+2-key.pem"

    )

    server = uvicorn.Server(config)
    server.run()