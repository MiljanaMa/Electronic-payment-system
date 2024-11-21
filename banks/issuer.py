from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/verify_transaction', methods=['POST'])
def verify_transaction():
    data = request.json

    required_fields = ["payment_id", "PAN", "SECURITY_CODE", "CARD_HOLDER_NAME", "CARD_EXPIRY_DATE"]
    if not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400

    # Simulacija provere stanja računa
    if data.get("PAN") == "valid_pan":  # Primer validne kartice
        return jsonify({"status": "success", "message": "Transaction approved"}), 200
    else:
        return jsonify({"status": "failed", "message": "Insufficient funds or invalid PAN"}), 400

if __name__ == '__main__':
    app.run(port=5003)
