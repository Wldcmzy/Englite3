from .utils.sqlApi import (
    adduser, 
    open_users_db, 
    deluser, 
    identify, 
    select_all_user,
    select_all,
)
from .utils.sysApi import usersdb, userpath
import sqlite3
import os


class Develop:
    def __init__(self) -> None:
        self.conn = open_users_db(usersdb)

    def operator(self):
        while True:
            print(self.menu())
            print()
            op = int(input('请选择:'))
            if op == 0:
                break
            elif op == 1:
                self.add()
            elif op == 2:
                self.del_()
            elif op == 3:
                self.idt()
            elif op == 4:
                self.seeall()
            elif op == 5:
                self.look_users_db()
    
    def run(self):
        self.operator()

    def menu(self):
        return '''
        0. 退出
        1. 添加用户
        2. 删除用户
        3. 确认
        4. 查看所有用户
        5. 查看一个用户目录的数据库
        '''.strip()
    
    def add(self):
        u = input('请输入用户名:')
        p = input('请输入密码:')
        adduser(self.conn, u, p)
    
    def del_(self):
        u = input('请输入用户名:')
        deluser(self.conn, u)

    def idt(self):
        u = input('请输入用户名:')
        p = input('请输入密码:')
        print('yes' if identify(self.conn, u, p) else 'deny')

    def seeall(self):
        for u, p, ad, t in  select_all_user(self.conn):
            print(u, ad, t)
    
    def look_users_db(self):
        username = input('username = ')
        dbname = input('dbname = ')
        if os.path.exists(userpath / username / dbname):
            conn = sqlite3.Connection(userpath / username / dbname)
            x = select_all(conn)
            print('总量:' + str(len(x)))
            print('前五个:' + str(x[:5]))
        else:
            print(str(userpath / username / dbname),'不存在')


    