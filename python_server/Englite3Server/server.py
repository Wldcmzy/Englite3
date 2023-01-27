import socket
import threading
from .utils.log import (
    logger,
    format_error
)
from .utils.sysApi import (
    get_db_list
)

class Server:
    QUERY_DB_LIST = 1
    SAVE_DB = 2
    LOAD_DB = 3

    SEP = '_|QuQ|_'
    END = '_|XvX|_'
    ERR = '@ERROR@'

    @staticmethod
    def decode(s: bytes) -> str:
        return s.decode('utf-8')
    
    @staticmethod
    def encode(s: str) -> bytes:
        return s.encode('utf-8')
    
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

    def __task_operator(self, sk: socket.socket):
        data = sk.recv(self.buffersize)
        data = int(self.decode(data))

        if data == self.QUERY_DB_LIST:
            self.__query_db_list(sk)
        elif data == self.SAVE_DB:
            pass
        elif data == self.LOAD_DB:
            pass
        else:
            pass
    
    def __query_db_list(self, sk: socket.socket):
        try:
            lst = get_db_list()
            for each in lst:
                data = self.ensure(each) + self.SEP
                sk.send(self.encode(data))
            sk.send(self.encode(self.END))
            sk.close()
            logger.info(f'线程{threading.current_thread().name}socket已关闭. 且线程结束')
        except Exception as e:
            format_error(e, f'线程名:{threading.current_thread().name}')

        
    def run(self) -> None:
        logger.info(f'服务器已开启, ip:{self.host} port:{self.port}')
        try:
            counter = 0
            while True:
                counter += 1
                sk, addr = self.socket.accept()
                task = threading.Thread(target = self.__task_operator, args=(sk, ), name = f'Englite3TCP_{counter}')
                task.start()
                logger.info(f"客户端: {addr} 链接, 分派线程 Englite3TCP_{counter}")
        except Exception as e:
            format_error(e)

