from pathlib import Path
import sqlite3
from .hash import generate_passcode
import datetime
from ..utils.log import logger

def open_users_db(path: Path) -> sqlite3.Connection:
    if not path.exists():
        conn = sqlite3.connect(path)
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE USERS(
            USERNAME        TEXT       PRIMARY KEY,
            PASSWORD        TEXT       ,
            LASTLOGINADDR   TEXT       ,
            LASTLOGINTIME   TEXT
            );
        ''')
    else:
        conn = sqlite3.connect(path)
    return conn

def adduser(conn: sqlite3.Connection, username: str, password: str) -> None:
    passcode = generate_passcode(password)
    c = conn.cursor()
    c.execute(f'''
        INSERT INTO USERS (USERNAME, PASSWORD, LASTLOGINADDR, LASTLOGINTIME)
        VALUES ("{username}", "{passcode}", "{"neverlogin"}", "{"neverlogin"}");
    ''')
    conn.commit()

def deluser(conn: sqlite3.Connection, username: str) -> None:
    c = conn.cursor()
    c.execute(f'''
        DELETE 
        FROM USERS
        WHERE USERNAME = "{username}";
    ''')
    conn.commit()
    

def identify(conn: sqlite3.Connection, username: str, password: str) -> bool:
    c = conn.cursor()
    c.execute(f'''
        SELECT USERNAME, PASSWORD
        FROM USERS
        WHERE USERNAME = "{username}"
    ''')
    data = c.fetchall()
    if len(data) <= 0: return False
    username, passcode  = data[0]
    return generate_passcode(password) == passcode

def set_user_last_login(conn: sqlite3.Connection, username: str, addr: tuple[int]) -> None:
    c = conn.cursor()
    c.execute(f'''
        UPDATE USERS
        SET 
        LASTLOGINADDR = "{addr}", 
        LASTLOGINTIME = "{str(datetime.datetime.now())}" 
        WHERE 
        USERNAME = "{username}" 
    ''')
    conn.commit()

def select_all_user(conn: sqlite3.Connection) -> tuple[tuple[str]]:
    c = conn.cursor()
    c.execute(f'select * from USERS')
    return c.fetchall()

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
        conn.commit()
    else:
        conn = sqlite3.connect(path)   
    return conn

def select_all(conn: sqlite3.Connection):
    c = conn.cursor()
    c.execute(f'select * from WORD')
    return c.fetchall()

def recreate_wordtable(conn: sqlite3.Connection):
    c = conn.cursor()
    c.execute(f'DROP TABLE WORD')
    conn.commit()
    c.execute('''
            CREATE TABLE WORD(
            EN           TEXT       PRIMARY KEY,
            CN           TEXT       ,
            PRONOUNCE    TEXT       ,
            COMBO        TEXT       ,
            LEVEL        INTEGER    ,
            E            INTEGER    ,
            FLAG         INTEGER    );
        ''')
    conn.commit()

def addone(
    # conn: sqlite3.Connection,
    cursor: sqlite3.Cursor,
    en: str, 
    cn: str, 
    pronounce: str, 
    combo: str,
    level: int,
    exponential: int,
    flag: int,
    ) -> None:
    # cursor = conn.cursor()
    cursor.execute(f'''
        INSERT INTO WORD (EN, CN, PRONOUNCE, COMBO, LEVEL, E, FLAG)
        VALUES ("{en}", "{cn}", "{pronounce}", "{combo}", "{level}", "{exponential}", "{flag}");
    ''')
    # conn.commit()
