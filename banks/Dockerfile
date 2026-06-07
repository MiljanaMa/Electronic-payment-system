# Single image for all three bank services (acquirer, pcc, issuer).
# Pick the service at runtime, e.g.:
#   docker run miljanama/banks flask --app aquirer run --host=0.0.0.0 --port=5000
#   docker run miljanama/banks flask --app pcc     run --host=0.0.0.0 --port=5002
#   docker run miljanama/banks flask --app issuer  run --host=0.0.0.0 --port=5003
FROM python:3.12-slim
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY *.py ./
# Pre-seeded SQLite databases
COPY instance ./instance

EXPOSE 5000 5002 5003
CMD ["flask", "--app", "aquirer", "run", "--host=0.0.0.0", "--port=5000"]
