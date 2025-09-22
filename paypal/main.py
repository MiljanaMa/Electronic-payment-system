from fastapi import FastAPI
import uvicorn
from payments import router

from seed import seed_data

app = FastAPI(title="PayPal Service")
app.include_router(router)

if __name__ == "__main__":
    seed_data()
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
    #seed_data()
