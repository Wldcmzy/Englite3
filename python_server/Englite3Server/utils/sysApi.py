from pathlib import Path
import os

dbpath = Path(__file__).parent.parent.parent/ "databases"

def get_db_list() -> list[str]:
    lst = os.listdir(dbpath)
    lst = filter(lambda x: x.endswith('.db'), lst)
    return list(lst)

def get_db_bytes(filename: str) -> bytes:
    with open(dbpath / filename, 'rb') as f:
        b = f.read()
    return b