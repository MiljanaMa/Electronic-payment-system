from database import SessionLocal, engine
from model import Base, Merchant
from database import get_db

# Kreira sve tabele ako ne postoje
Base.metadata.create_all(bind=engine)

def seed_data():
    #Base.metadata.create_all(bind=engine)
    db = next(get_db())
    try:
        db = SessionLocal()
        merchants = db.query(Merchant).all()
        for m in merchants:
            print(m.merchant_id, m.paypal_client_id, m.paypal_secret)
        # Proveri da li vec ima usera
        user2 = db.query(Merchant).first()
        user2.paypal_client_id = "AQnfSgZ6fao-f_NeVupeLsulKlzg596_sYu2PLhTy7tQWmC4hBZKSjKyUBKAd4WVQuCmB1VLzd-M4anJ"
        user2.paypal_secret = "EHrnTGP7wjnhRqWUuq3FBMoZKZ7tiFRh60sk-0NdNtO-gasV_FVU4Nzklqs5EKZ945ZlI3GYDZ5_ud6Y"
        db.commit()
        """"
        if not db.query(Transaction).first():
            t1 = Transaction(amount=100, status="PENDING", user_id=1)
            t2 = Transaction(amount=200, status="COMPLETED", user_id=2)
            db.add_all([t1, t2])
            db.commit()
        """
        print("✅ Seed data ubacen!")
    finally:
        db.close()
        
if __name__ == "__main__":
    seed_data()