import hashlib

def generate_passcode(password: str) -> str:
    ret = hashlib.md5(password.encode(encoding='utf-8')).hexdigest()
    ret += hashlib.sha256(password.encode(encoding='utf-8')).hexdigest()
    return ret