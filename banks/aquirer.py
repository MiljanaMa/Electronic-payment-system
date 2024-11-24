import uuid
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
import requests
from datetime import datetime, timezone


# Inicijalizacija aplikacije i baze podataka
app = Flask(__name__)
CORS(app)  # Enable CORS for the entire app
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///payments.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

# Modeli za bazu podataka
class PaymentTransaction(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    merchant_id = db.Column(db.String(50), nullable=False)
    amount = db.Column(db.Float, nullable=False)
    merchant_order_id = db.Column(db.String(50), nullable=False)
    merchant_timestamp = db.Column(db.String(50), nullable=False)
    success_url = db.Column(db.String(200), nullable=False)
    failed_url = db.Column(db.String(200), nullable=False)
    error_url = db.Column(db.String(200), nullable=False)
    payment_id = db.Column(db.String(50), unique=True, nullable=False)
    payment_url = db.Column(db.String(200), nullable=False)
    status = db.Column(db.String(50), default="Pending")

    def __repr__(self):
        return f'<PaymentTransaction {self.payment_id}>'
    
class BankAccount(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    account_number = db.Column(db.String(20), unique=True, nullable=False)
    holder_name = db.Column(db.String(100), nullable=False)
    balance = db.Column(db.Float, nullable=False, default=0.0)

    # Veza sa karticama
    cards = db.relationship('Card', backref='account', lazy=True)

    def __repr__(self):
        return f'<BankAccount {self.account_number}>'

# Model za Card
class Card(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    card_number = db.Column(db.String(16), unique=True, nullable=False)
    cvv = db.Column(db.String(3), nullable=False)
    expiration_date = db.Column(db.String(7), nullable=False)  # Format: MM/YYYY
    holder_name = db.Column(db.String(100), nullable=False)
    account_number = db.Column(db.String(20), db.ForeignKey('bank_account.account_number'), nullable=False)

    def __repr__(self):
        return f'<Card {self.card_number}>'

class Merchant(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    merchant_id = db.Column(db.String(50), unique=True, nullable=False)
    name = db.Column(db.String(100), nullable=False)
    password = db.Column(db.String(100), nullable=False)
    bank_account_number = db.Column(db.String(50), nullable=False)  # Broj bankovnog računa

    def __repr__(self):
        return f'<Merchant {self.merchant_id}>'

@app.route('/create_merchant', methods=['POST'])
def create_merchant():
    merchants_data = [
        {
            "merchant_id": "MERCHANT001",
            "name": "Merchant One",
            "password": "password123",
            "bank_account_number": "1234567890"
        },
        {
            "merchant_id": "MERCHANT002",
            "name": "Merchant Two",
            "password": "password456",
            "bank_account_number": "0987654321"
        },
        {
            "merchant_id": "MERCHANT003",
            "name": "Merchant Three",
            "password": "password789",
            "bank_account_number": "1122334455"
        }
    ]
    
    # Dodavanje svakog merchant-a u bazu podataka
    for merchant_data in merchants_data:
        merchant = Merchant(
            merchant_id=merchant_data['merchant_id'],
            name=merchant_data['name'],
            password=merchant_data['password'],
            bank_account_number=merchant_data['bank_account_number']
        )
        db.session.add(merchant)
    
    db.session.commit()

    return jsonify({"message": "Merchants created successfully"}), 201


# Kreiranje tabele u bazi (ako ne postoji)
with app.app_context():
    db.create_all()

# Generisanje PAYMENT_ID i PAYMENT_URL
@app.route('/generate_payment', methods=['POST'])
def generate_payment():
    data = request.json
    required_fields = ["MERCHANT_ID", "MERCHANT_PASSWORD", "AMOUNT", "MERCHANT_ORDER_ID", 
                       "MERCHANT_TIMESTAMP", "SUCCESS_URL", "FAILED_URL", "ERROR_URL"]
    
    # Provera validnosti ulaznih podataka
    if not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400

    # Generisanje PAYMENT_ID i PAYMENT_URL
    payment_id = f"PAY-{data['MERCHANT_ORDER_ID']}"
    payment_url = f"http://localhost:5000/process_payment/{payment_id}"

    # Snimanje podataka u bazu
    payment = PaymentTransaction(
        merchant_id=data['MERCHANT_ID'],
        amount=data['AMOUNT'],
        merchant_order_id=data['MERCHANT_ORDER_ID'],
        merchant_timestamp=data['MERCHANT_TIMESTAMP'],
        success_url=data['SUCCESS_URL'],
        failed_url=data['FAILED_URL'],
        error_url=data['ERROR_URL'],
        payment_id=payment_id,
        payment_url=payment_url
    )
    db.session.add(payment)
    db.session.commit()

    return jsonify({"PAYMENT_ID": payment_id, "PAYMENT_URL": payment_url}), 200

# Obrada plaćanja
@app.route('/process_payment/<payment_id>', methods=['POST'])
def process_payment(payment_id):
    data = request.json
    required_fields = ["PAN", "SECURITY_CODE", "CARD_HOLDER_NAME", "CARD_EXPIRY_DATE"]
    
    if not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400
    
    payment = PaymentTransaction.query.filter_by(payment_id=payment_id).first()
    if not payment:
        return jsonify({"error": "Payment ID not found"}), 404

    # Proverava da li merchant postoji u bazi
    merchant = Merchant.query.filter_by(merchant_id=payment.merchant_id).first()
    print(f"merchantid: {payment.merchant_id}")
    if not merchant:
        return jsonify({"error": "Merchant not found"}), 404
    print(f"Merchant Name: {merchant.name}")

    creditCard1 = Card.query.filter_by(card_number=data["PAN"]).first()
    if not creditCard1:
        acquirer_order_id = str(uuid.uuid4())
        acquirer_timestamp = datetime.now(timezone.utc).isoformat()

        # Podaci za slanje ka PCC
        pcc_request = {
            "ACQUIRER_ORDER_ID": acquirer_order_id,
            "ACQUIRER_TIMESTAMP": acquirer_timestamp,
            "PAN": data["PAN"],
            "SECURITY_CODE": data["SECURITY_CODE"],
            "CARD_HOLDER_NAME": data["CARD_HOLDER_NAME"],
            "CARD_EXPIRY_DATE": data["CARD_EXPIRY_DATE"],
            "AMOUNT": payment.amount,
            "MERCHANT_ID": merchant.merchant_id,
            "MERCHANT_ACCOUNT_NUMBER": merchant.bank_account_number,
        }

        try:
            # Slanje POST zahteva ka PCC
            pcc_response = requests.post("http://localhost:5002/process_payment", json=pcc_request)
            
            if pcc_response.status_code == 200:
                return jsonify({"message": "Payment processed via PCC.", "response": pcc_response.json()}), 200
            else:
                return jsonify({"error": "PCC processing failed.", "details": pcc_response.json()}), pcc_response.status_code
        except requests.exceptions.RequestException as e:
            return jsonify({"error": "Failed to communicate with PCC.", "details": str(e)}), 500
    # Provera da li se podaci sa kartice poklapaju
    if creditCard1.cvv != data["SECURITY_CODE"]:
        return jsonify({"error": "Invalid security code"}), 400

    if creditCard1.holder_name != data["CARD_HOLDER_NAME"]:
        return jsonify({"error": "Card holder name mismatch"}), 400

    if creditCard1.expiration_date != data["CARD_EXPIRY_DATE"]:
        return jsonify({"error": "Card expiry date mismatch"}), 400
    
    # Izvlači broj računa merchant-a
    merchant_account_number = merchant.bank_account_number
    print(f"Merchant account: {merchant_account_number}")
    
    # Uzimamo prvih 3 cifre broja računa merchant-a
    merchant_account_prefix = merchant_account_number[:3]
    print(f"merchant_account_prefix: {merchant_account_prefix}")
    # Izvlači broj računa kupca (to je PAN broj)
    creditCard = Card.query.filter_by(card_number=data["PAN"]).first()
    if not creditCard:
        return jsonify({"error": "Card not found"}), 404
    customer_account1 = creditCard.account_number

    customer_account_prefix = customer_account1[:3]
    print(f"customer_account_prefix: {customer_account_prefix}")

    # Proverava da li su prve 3 cifre broja računa iste
    if merchant_account_prefix == customer_account_prefix:
        # Provera stanja na računu kupca
        customer_account = BankAccount.query.filter_by(account_number=customer_account1).first()
        print(f"bank account: {customer_account.account_number}")
        if not customer_account:
            return jsonify({"error": "Customer account not found"}), 404
        
        # Proverava da li kupac ima dovoljno sredstava
        if customer_account.balance < payment.amount:
            return jsonify({"error": "Insufficient funds"}), 400
        
        print(f"customer_account balance before: {customer_account.balance}")
        customer_account.balance -= payment.amount
        print(f"customer_account balance after: {customer_account.balance}")
        db.session.commit()  # Spremanje promena u bazi
        merchant_account5 = BankAccount.query.filter_by(account_number=merchant_account_number).first()
        merchant_account5.balance += payment.amount
        db.session.commit()  # Spremanje promena u bazi

        
        # Ako su banke iste i kupac ima dovoljno sredstava
        return jsonify({"message": "Payment approved and processed."}), 200
    else:
        # Različite banke
        acquirer_order_id = str(uuid.uuid4())
        acquirer_timestamp = datetime.now(timezone.utc).isoformat()

        # Podaci za slanje ka PCC
        pcc_request = {
            "ACQUIRER_ORDER_ID": acquirer_order_id,
            "ACQUIRER_TIMESTAMP": acquirer_timestamp,
            "PAN": data["PAN"],
            "SECURITY_CODE": data["SECURITY_CODE"],
            "CARD_HOLDER_NAME": data["CARD_HOLDER_NAME"],
            "CARD_EXPIRY_DATE": data["CARD_EXPIRY_DATE"],
            "AMOUNT": payment.amount,
            "MERCHANT_ID": merchant.merchant_id,
            "MERCHANT_ACCOUNT_NUMBER": merchant_account_number,
        }

        try:
            # Slanje POST zahteva ka PCC
            pcc_response = requests.post("http://localhost:5002/process_payment", json=pcc_request)
            
            if pcc_response.status_code == 200:
                return jsonify({"message": "Payment processed via PCC.", "response": pcc_response.json()}), 200
            else:
                return jsonify({"error": "PCC processing failed.", "details": pcc_response.json()}), pcc_response.status_code
        except requests.exceptions.RequestException as e:
            return jsonify({"error": "Failed to communicate with PCC.", "details": str(e)}), 500

@app.route('/add_sample_data', methods=['GET'])
def add_sample_data():
    # Fiksni podaci za bankovne račune
    bank_account_data = [
        {
            "account_number": "1234567890123456",
            "holder_name": "John Doe",
            "balance": 1000.0,
            "cards": [
                {"card_number": "4111111111111111", "cvv": "123", "expiration_date": "12/25", "holder_name": "John Doe"},
                {"card_number": "5500000000000004", "cvv": "321", "expiration_date": "06/24", "holder_name": "John Doe"}
            ]
        },
        {
            "account_number": "6543219876543210",
            "holder_name": "Jane Smith",
            "balance": 2000.0,
            "cards": [
                {"card_number": "4111111111112222", "cvv": "456", "expiration_date": "01/26",  "holder_name": "Jane Smith"},
                {"card_number": "5500000000003333", "cvv": "654", "expiration_date": "05/25", "holder_name": "Jane Smith"}
            ]
        },
        {
            "account_number": "9876543210987654",
            "holder_name": "Alice Johnson",
            "balance": 500.0,
            "cards": [
                {"card_number": "4111111111113333", "cvv": "789", "expiration_date": "11/24",  "holder_name": "Alice Johnson"},
                {"card_number": "5500000000004444", "cvv": "987", "expiration_date": "02/25",  "holder_name": "Alice Johnson"}
            ]
        }
    ]

    # Dodaj bankovne račune i kartice
    for account in bank_account_data:
        bank_account = BankAccount(
            account_number=account["account_number"],
            holder_name=account["holder_name"],
            balance=account["balance"]
        )
        
        # Dodaj kartice za svaki bankovni račun
        for card in account["cards"]:
            card_obj = Card(
                card_number=card["card_number"],
                cvv=card["cvv"],
                expiration_date=card["expiration_date"],
                holder_name=card["holder_name"],
                account_number=account["account_number"]  # Veza sa bankovnim računom
            )
            db.session.add(card_obj)

        # Dodaj bankovni račun u bazu
        db.session.add(bank_account)

    # Spremi sve promene u bazu
    db.session.commit()

    return jsonify({"message": "Sample data added successfully"}), 200



if __name__ == '__main__':
    app.run(port=5000)
