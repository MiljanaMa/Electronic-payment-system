from datetime import datetime, timezone
import uuid
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Enable CORS for the entire app
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///issuer.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

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

    
with app.app_context():
    db.create_all()

@app.route('/process_payment', methods=['POST'])
def process_payment():
    data = request.json
    required_fields = ["PAN", "SECURITY_CODE", "CARD_HOLDER_NAME", "CARD_EXPIRY_DATE", "AMOUNT", "MERCHANT_ACCOUNT_NUMBER"]

    if not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400

    # Pronalazi karticu na osnovu PAN-a
    card = Card.query.filter_by(card_number=data["PAN"]).first()
    if not card:
        return jsonify({"error": "Card not found ISSUER"}), 404

    # Provera podataka sa kartice
    if card.cvv != data["SECURITY_CODE"]:
        return jsonify({"error": "Invalid security code"}), 400
    if card.holder_name != data["CARD_HOLDER_NAME"]:
        return jsonify({"error": "Card holder name mismatch"}), 400
    if card.expiration_date != data["CARD_EXPIRY_DATE"]:
        return jsonify({"error": "Card expiry date mismatch"}), 400

    # Provera stanja na računu
    customer_account = BankAccount.query.filter_by(account_number=card.account_number).first()
    if customer_account.balance < data["AMOUNT"]:
        return jsonify({"error": "Insufficient funds"}), 400

    # Umanjuje stanje kupca i uvećava merchant-ov račun
    customer_account.balance -= data["AMOUNT"]

    db.session.commit()

    issuer_order_id = str(uuid.uuid4())  # Generisanje novog UUID za Issuer
    issuer_timestamp = datetime.now(timezone.utc).isoformat()  # Trenutni timestamp za Issuer

    # Pretpostavljamo da se transakcija ovde proverava (da li je iznos ispravan, validacija kartice, itd.)
    # Ovde možete dodati logiku za proveru da li je transakcija odobrena ili odbijena
    transaction_status = "SUCCESS"  # Ili "FAILURE" u zavisnosti od validacije

    # Kreiranje odgovora za PCC
    issuer_response = {
        "issuer_order_id": issuer_order_id,
        "issuer_timestamp": issuer_timestamp,
        "transaction_status": transaction_status  # Možete dodati više informacija prema potrebama
    }

    return jsonify(issuer_response), 200
if __name__ == '__main__':
    app.run(port=5003)
