FROM python:3.12-slim
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY *.py ./
COPY services ./services
COPY utils ./utils
COPY certs ./certs
# Pre-seeded SQLite database
COPY paypal.db ./paypal.db

EXPOSE 8087
# Same TLS certs as the manual setup (issued for localhost),
# served via uvicorn instead of the Windows paths in main.py
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8087", \
     "--ssl-certfile", "certs/localhost+2.crt", \
     "--ssl-keyfile", "certs/localhost+2-key.pem"]
