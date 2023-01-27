import logging
from pathlib import Path
import time

now_time = str(time.localtime()[ : 6])[1 : -1].replace(', ', '-')
logpath = Path(__file__).parent.parent.parent / 'logs' / f'start at {now_time}.log'

__handlerF = logging.FileHandler(logpath)
__handlerF.setLevel(logging.DEBUG)
__handlerC = logging.StreamHandler()
__handlerC.setLevel(logging.INFO)
__formatter = logging.Formatter('%(asctime)s <%(levelname)s>: %(message)s')
__handlerF.setFormatter(__formatter)
__handlerC.setFormatter(__formatter)
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logger.addHandler(__handlerF)
logger.addHandler(__handlerC)

def format_error(e: Exception, extra: str = '') -> None:
    logger.error(f'{type(e)} {str(e)} | extra:{extra}')
