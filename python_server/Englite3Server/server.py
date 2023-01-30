import socket
import threading
import random
from .utils.log import (
    logger,
    format_error,
)
from .utils.sysApi import (
    dbpath,
    get_db_list,
)
from .utils.sqlApi import (
    opendb,
    select_all,
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
        data = data.encode('utf-8')
        if mod == cls.AESMOD:
            pass
        sk.send(data)

    @classmethod
    def Crecv(cls, sk: socket.socket, mod: int, buffersize: int, key: str = None) -> str:
        data = sk.recv(buffersize)
        if mod == cls.AESMOD:
            data = data.decode('utf-8')
        elif mod == cls.RSAMOD:
            data = data.decode('utf-8')
        elif mod == cls.NONEMOD:
            data = data.decode('utf-8')
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

        re = 'sdfsdf'
        # if random.randint(0, 1) == 0:
        #     re = self.DENY
        #     logger.warning("生成一个拒绝")
        self.Csend(sk, re, self.AESMOD)
    
        num = int(data[2])
        if num ==self.SAVE_DB or num == self.LOAD_DB:
            dbname = data[3]

        if num == self.QUERY_DB_LIST:
            logger.info(f'{threading.current_thread().name} addr={addr} query database list.')
            self.__query_db_list(sk)
        elif num == self.SAVE_DB:
            pass
        elif num == self.LOAD_DB:
            logger.info(f'{threading.current_thread().name} addr={addr} wanna download database, name = {dbname}')
            self.__send_db_info(sk, dbname)
        else:
            pass
    
    def __query_db_list(self, sk: socket.socket):
        try:
            lst = get_db_list()
            for each in lst:
                data = self.ensure(each) + self.SEP
                self.Csend(sk, data, self.AESMOD)
            self.Csend(sk, self.END, self.AESMOD)
            sk.close()
            logger.info(f'线程{threading.current_thread().name}socket已关闭. 且线程结束')
        except Exception as e:
            format_error(e, f'线程名:{threading.current_thread().name}')

    def __send_db_info(self, sk: socket.socket, dbname: str):
        try:
            lst = get_db_list(descirption=False)
            if dbname not in lst:
                self.Csend(sk, self.NODB, self.AESMOD)
                logger.info(f'database {dbname} not exist.')
            else:
                conn = opendb(dbpath / dbname)
                words = select_all(conn)
                for en, cn, pron, combo, level, e, sta in words:
                    data = ''
                    data += en + self.WORDSEP
                    data += cn + self.WORDSEP
                    data += pron + self.WORDSEP
                    data += combo + self.WORDSEP
                    data += str(level) + self.WORDSEP
                    data += str(e) + self.WORDSEP
                    data += str(sta)
                    data += self.SEP

                    self.Csend(sk, data, self.AESMOD)

            self.Csend(sk, self.END, self.AESMOD)
            sk.close()
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

