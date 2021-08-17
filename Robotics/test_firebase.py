import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate("service.json")

firebase_admin.initialize_app(cred, {"databaseURL": "https://mapsgps-fd863-default-rtdb.europe-west1.firebasedatabase.app/"})

ref = db.reference("/")
