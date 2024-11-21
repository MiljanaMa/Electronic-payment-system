from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

@app.route('/process_transaction', methods=['POST'])
def process_transaction():
    data = request.json
    
    required_fields = ["payment_id", "PAN", "SECURITY_CODE", "CARD_HOLDER_NAME", "CARD_EXPIRY_DATE"]
    if not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400
    
    # Provera PAN-a da se poveže sa Bankom kupca
    issuer_url = "http://localhost:5003/verify_transaction"
    response = requests.post(issuer_url, json=data)
    
    return jsonify(response.json())

if __name__ == '__main__':
    app.run(port=5002)
