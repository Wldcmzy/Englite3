import socket
import threading
import random
import os
import time
from .utils.log import (
    logger,
    format_error,
)
from .utils.sysApi import (
    dbpath,
    userpath,
    usersdb,
    get_db_list,
)
from .utils.sqlApi import (
    opendb,
    select_all,
    recreate_wordtable,
    addone,

    open_users_db,
    identify,
    set_user_last_login,
)

class Server:
    QUERY_DB_LIST = 1
    SAVE_DB = 2
    LOAD_DB = 3

    SEP = '_#QuQ#_'
    END = '_#XvX#_'
    ERR = '@ERROR@'
    WORDSEP = '_#>_<#_'

    AESMOD = 1
    RSAMOD = 2
    NONEMOD = 3

    NODB = "NODB"
    DENY = "DENY"

    # @staticmethod
    # def decode(s: bytes) -> str:
    #     return s.decode('utf-8')
    
    # @staticmethod
    # def encode(s: str) -> bytes:
    #     return s.encode('utf-8')

    @classmethod
    def Csend(cls, sk: socket.socket, data: str, mod: int, key: str = None) -> None:
        x = data
        data = data.encode('utf-8')
        if mod == cls.AESMOD:
            pass
        
        while len(data) > 1024:
            sk.send(data[ : 1024])
            data = data[1024 : ]

        sk.send(data)

    @classmethod
    def Crecv(cls, sk: socket.socket, mod: int, buffersize: int, key: str = None) -> str:
        data = sk.recv(buffersize)
        if mod == cls.AESMOD:
            data = data.decode('utf-8', "replace")
        elif mod == cls.RSAMOD:
            data = data.decode('utf-8', "replace")
        elif mod == cls.NONEMOD:
            data = data.decode('utf-8', "replace")
        return data

    @classmethod
    def ensure(cls, s: str) -> str:
        s = s.replace(cls.SEP, cls.ERR)
        s = s.replace(cls.END, cls.ERR)
        return s
    
    def __init__(
        self, 
        addr: tuple[str, int], 
        listen_max: int = 10,
        buffersize: int = 2048,
    ) -> None:
        self.buffersize = buffersize
        self.host, self.port = addr
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind(addr)
        self.socket.listen(listen_max)

    def __task_operator(self, sk: socket.socket, addr: tuple[str, int]):
        aeskey = self.Crecv(sk, self.RSAMOD, self.buffersize)
        self.Csend(sk, str(random.randint(9999, 99999)), self.AESMOD)

        data = self.Crecv(sk, self.RSAMOD, self.buffersize)
        data = data.split('_')
        username = data[0]
        password = data[1]
        conn = open_users_db(usersdb)
        if identify(conn, username, password) == False:
            self.Csend(sk, self.DENY, self.AESMOD)
            logger.info(f'{threading.current_thread().name} addr={addr} username={username} 身份验证失败, 线程结束')
            sk.close()
            return
        else:
            set_user_last_login(conn, username, addr)
            self.Csend(sk,'something', self.AESMOD)
            logger.info(f'{threading.current_thread().name} addr={addr} username={username} 身份验证成功')
    
        conn.close()

        num = int(data[2])
        if num ==self.SAVE_DB or num == self.LOAD_DB:
            dbname = data[3]

        if num == self.QUERY_DB_LIST:
            logger.info(f'{threading.current_thread().name} addr={addr} query database list.')
            self.__query_db_list(sk)
        elif num == self.SAVE_DB:
            logger.info(f'{threading.current_thread().name} addr={addr} wanna upload database, name = {dbname}')
            self.__receive_db_info(sk, username, dbname)
        elif num == self.LOAD_DB:
            logger.info(f'{threading.current_thread().name} addr={addr} wanna download database, name = {dbname}')
            self.__send_db_info(sk, dbname)
        else:
            pass
    
    def __query_db_list(self, sk: socket.socket):
        try:
            lst = get_db_list()
            cnt = 0
            for each in lst:
                data = self.ensure(each) + self.SEP
                self.Csend(sk, data, self.AESMOD)
                if cnt >= 100:
                    cnt -= 100
                    time.sleep(0.01)
            self.Csend(sk, self.END, self.AESMOD)
            sk.close()
            logger.info(f'线程{threading.current_thread().name}socket已关闭. 且线程结束')
        except Exception as e:
            format_error(e, f'线程名:{threading.current_thread().name}')

    def __send_db_info(self, sk: socket.socket, dbname: str):
        try:
            lst = get_db_list(descirption=False)
            if dbname not in lst:
                self.Csend(sk, self.NODB + self.SEP, self.AESMOD)
                logger.info(f'database {dbname} not exist.')
            else:
                conn = opendb(dbpath / dbname)
                words = select_all(conn)
                for en, cn, pron, combo, level, e, flag in words:
                    data = f'{en}{self.WORDSEP}{cn}{self.WORDSEP}{pron}{self.WORDSEP}{combo}{self.WORDSEP}{level}{self.WORDSEP}{e}{self.WORDSEP}{flag}{self.SEP}'

                    self.Csend(sk, data, self.AESMOD)

            self.Csend(sk, self.END, self.AESMOD)
            sk.close()
            logger.info(f'database {dbname} 传输完成.')
        except Exception as e:
            format_error(e, f'线程名:{threading.current_thread().name}')

    def __receive_db_info(self, sk: socket.socket, username: str, dbname: str):
        try:
            if not os.path.exists(userpath / username):
                os.makedirs(userpath / username)
            
            conn = opendb(userpath / username / dbname)
            recreate_wordtable(conn)
            logger.info('recreate ok')
            c = conn.cursor()

            can_exit = False
            remain = ''
            no_end = False
            lst = []
            while True:
                data = self.Crecv(sk, self.AESMOD, self.buffersize)
                if len(remain) > 0:
                    data = remain + data
                    remain = ''
                
                if data.endswith(self.END):
                    can_exit = True
                    data = data[ : -len(self.END)]
                
                if data != '':
                    if data.endswith(self.SEP):
                        data = data[ : -len(self.SEP)]
                        no_end = False
                    else:
                        no_end = True

                    datas = data.split(self.SEP)
                
                    for i in range(len(datas)):
                        info = datas[i]
                        if i == len(datas) - 1 and no_end == True:
                            remain = info
                        else:
                            # logger.info(str(info.split(self.WORDSEP)))
                            en, cn, pron, combo, level, e, flag = info.split(self.WORDSEP)
                            level, e, flag = int(level), int(e), int(flag)
                            lst.append((en, cn, pron, combo, level, e, flag))

                if can_exit:
                    print('len lst =', len(lst))
                    for each in lst:
                        addone(c, *each)
                        # print(each[0])
                    conn.commit()
                    # print(len(lst))
                    logger.info(f'{threading.current_thread().name} database upload finished dbname = {dbname}')
                    break

        except Exception as e:
            format_error(e, f'线程名:{threading.current_thread().name}')

        
    def run(self) -> None:
        logger.info(f'服务器已开启, ip:{self.host} port:{self.port}')
        try:
            counter = 0
            while True:
                counter += 1
                sk, addr = self.socket.accept()
                task = threading.Thread(target = self.__task_operator, args=(sk, addr,), name = f'Englite3TCP_{counter}')
                task.start()
                logger.info(f"客户端: {addr} 链接, 分派线程 Englite3TCP_{counter}")
        except Exception as e:
            format_error(e)

