from pathlib import Path
import sqlite3

def opendb(path: Path) -> sqlite3.Connection:
    if not path.exists():
        conn = sqlite3.connect(path)
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE WORD(
            EN           TEXT       PRIMARY KEY,
            CN           TEXT       ,
            PRONOUNCE    TEXT       ,
            COMBO        TEXT       ,
            LEVEL        INTEGER    ,
            E            INTEGER    ,
            FLAG         INTEGER    );
        ''')
    else:
        conn = sqlite3.connect(path)   
    return conn

def select_all(conn: sqlite3.Connection):
    c = conn.cursor()
    c.execute(f'select * from WORD')
    return c.fetchall()
