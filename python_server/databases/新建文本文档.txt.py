import sqlite3 

NAME = 'undertale' + '.db'
conn = sqlite3.Connection(NAME)
cs = conn.cursor()
sql = 'select * from WORD'
cs.execute(sql)
res = cs.fetchall()
print(len(res))