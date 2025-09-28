import ssl
from fastapi import FastAPI
import uvicorn
from router import router
from seed import seed_data

app = FastAPI(title="PayPal Service")
app.include_router(router)

import ssl
from fastapi import FastAPI
from router import router
from seed import seed_data
import uvicorn

app = FastAPI(title="PayPal Service")
app.include_router(router)

if __name__ == "__main__":
    seed_data()

    # Direktno prosleđivanje cert i key fajlova
    config = uvicorn.Config(
        app=app,
        host="0.0.0.0",
        port=8000,
        reload=True,
        ssl_certfile="./certs/localhost+2.crt",
        ssl_keyfile="./certs/localhost+2-key.pem"
    )

    server = uvicorn.Server(config)
    server.run()