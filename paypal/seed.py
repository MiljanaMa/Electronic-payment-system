from database import SessionLocal, engine
from model import Base, Merchant
from database import get_db

# Kreira sve tabele ako ne postoje
Base.metadata.create_all(bind=engine)

def seed_data():
    #Base.metadata.create_all(bind=engine)
    db = next(get_db())
    try:
        # Proveri da li vec ima usera
        if not db.query(Merchant).first():
            user1 = Merchant(merchant_id="123456", paypal_client_id="AWxmxxO_s2I9BserUKGNEkqTFsNYhXWLVIHx2xk4NYUDUaPFHbVdlOe_HkPxwmVPryHPgVHOKndLjI8h",
                             paypal_secret="ELOowKx_lT_Agey_usSDLSRZ_wJUvwM6pHE6M9jzrwvqCCxndOhuHlg_Y7IL28torzAOMhv-gdzhjsvo")
            db.add_all([user1])
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