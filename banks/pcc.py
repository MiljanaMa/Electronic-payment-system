import uuid
from flask import Flask, request, jsonify
import requests
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Enable CORS for the entire app
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///pcc.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

class Transaction(db.Model):
    __tablename__ = 'transactions'
    
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    transaction_id = db.Column(db.String(36), unique=True, nullable=False)  # UUID za transakciju
    acquirer_order_id = db.Column(db.String(36), nullable=False)  # Jedinstveni ID banke prodavca
    acquirer_timestamp = db.Column(db.String(50), nullable=False)  # Vreme zahteva od strane banke prodavca
    issuer_order_id = db.Column(db.String(36), nullable=False)  # Jedinstveni ID banke kupca
    issuer_timestamp = db.Column(db.String(50), nullable=False)  # Vreme odgovora banke kupca
    
    masked_pan = db.Column(db.String(19), nullable=False)  # Maskirani PAN (poslednje 4 cifre)
    acquirer_bank = db.Column(db.String(50), nullable=False)  # Naziv ili ID banke prodavca
    issuer_bank = db.Column(db.String(50), nullable=False)  # Naziv ili ID banke kupca
    payment_amount = db.Column(db.Float, nullable=False)  # Iznos transakcije
    
    transaction_status = db.Column(db.String(20), nullable=False)  # Status transakcije (SUCCESS, FAILURE, ERROR)
    def __repr__(self):
        return f"<Transaction {self.transaction_id}, Status: {self.transaction_status}>"

with app.app_context():
    db.create_all()

@app.route('/process_payment', methods=['POST'])
def process_transaction():
    data = request.json

    # Proverava da li su svi potrebni podaci prisutni
    required_fields = ["ACQUIRER_ORDER_ID", "ACQUIRER_TIMESTAMP","PAN", "SECURITY_CODE", "CARD_HOLDER_NAME", "CARD_EXPIRY_DATE", "AMOUNT", "MERCHANT_ID", "MERCHANT_ACCOUNT_NUMBER"]
    if not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400

    # Maskiranje PAN-a za sigurnost (prikaz samo poslednje 4 cifre)
    masked_pan = f"**** **** **** {data['PAN'][-4:]}"
    acquirer_order_id = data["ACQUIRER_ORDER_ID"]
    acquirer_timestamp = data["ACQUIRER_TIMESTAMP"]
    # Kreiranje zahteva za Issuer
    issuer_request = {
        "PAN": data["PAN"],
        "SECURITY_CODE": data["SECURITY_CODE"],
        "CARD_HOLDER_NAME": data["CARD_HOLDER_NAME"],
        "CARD_EXPIRY_DATE": data["CARD_EXPIRY_DATE"],
        "AMOUNT": data["AMOUNT"],
        "MERCHANT_ACCOUNT_NUMBER": data["MERCHANT_ACCOUNT_NUMBER"]
    }

    try:
        # Slanje POST zahteva ka Issuer servisu (port 5003)
        issuer_response = requests.post("http://localhost:5003/process_payment", json=issuer_request)
        if issuer_response.status_code == 200:
            issuer_data = issuer_response.json()
            # Kreiranje nove transakcije i čuvanje u bazi PCC
            new_transaction = Transaction(
                transaction_id=str(uuid.uuid4()),  # Generisanje novog UUID za transakciju
                acquirer_order_id=acquirer_order_id,
                acquirer_timestamp=acquirer_timestamp,
                issuer_order_id = issuer_data["issuer_order_id"],
                issuer_timestamp = issuer_data["issuer_timestamp"],
                transaction_status = issuer_data["transaction_status"],
                masked_pan=masked_pan,
                acquirer_bank=data["MERCHANT_ACCOUNT_NUMBER"][:3],  # Prvih 3 cifre računa prodavca
                issuer_bank=data["MERCHANT_ACCOUNT_NUMBER"][:3],  # Prvih 3 cifre računa kupca
                payment_amount=data["AMOUNT"]
            )

            db.session.add(new_transaction)
            db.session.commit()
            return jsonify({
                "message": "Transaction processed successfully.",
                "issuer_order_id": issuer_data["issuer_order_id"],
                "issuer_timestamp": issuer_data["issuer_timestamp"]
            }), 200
        else:
            return jsonify({
                "error": "Transaction failed at Issuer.",
                "issuer_response": issuer_response.json()
            }), issuer_response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({"error": "Failed to communicate with Issuer.", "details": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5002)
