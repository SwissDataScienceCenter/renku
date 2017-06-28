"""setuptools setup.py"""
from setuptools import setup, find_packages

setup(
    name='sdsc_sdk',
    version='0.1',
    packages=['sdsc_sdk'],
    install_requires=['requests'],
)
