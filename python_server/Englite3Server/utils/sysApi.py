from pathlib import Path
import os

dbpath = Path(__file__).parent.parent.parent/ "databases"
userpath = Path(__file__).parent.parent.parent/ "users"
usersdb = Path(__file__).parent.parent.parent/ "users" / "users.db"

def get_db_list(descirption: bool = True) -> list[str]:
    lst = []
    all_files = os.listdir(dbpath)
    db_files = list(filter(lambda x: x.endswith('.db'), all_files))
    if descirption:
        for dbname in db_files:
            statement = '无描述'
            statement_file = dbname + '.txt'
            if statement_file in all_files:
                with open(dbpath / statement_file, 'r', encoding='utf-8') as f:
                    statement = f.read()
            lst.append(f'{dbname}_{statement}')
    else:
        lst = db_files
    return lst
        
    

def get_db_bytes(filename: str) -> bytes:
    with open(dbpath / filename, 'rb') as f:
        b = f.read()
    return b