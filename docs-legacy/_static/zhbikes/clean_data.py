# -*- coding: utf-8 -*-
#
# Simple module for processing public bike counter data from Zürich,
# Switzerland.
#

import pandas as pd

def read_data(input_dir):
    """
    Read all the data from input_dir into a pandas dataframe.

    Args:

        input_dir: the directory where data is stored
    """
    from glob import glob

    input_files = glob('{}/*.csv'.format(input_dir))
    dataframes = [
        pd.read_csv(file)[[
            'fk_zaehler', 'datum', 'velo_in', 'velo_out', 'fuss_in',
            'fuss_out', 'objectid'
        ]] for file in input_files
    ]
    df = pd.concat(dataframes)
    return df


def clean_data(input_dir, output_path=None):
    """
    Do some basic cleaning:

    * remove non-necessary columns
    * remove NaNs
    * translate to English.
    * convert datetime strings to Datetime objects

    Args:

        input_dir: the directory where the data is stored

        output_path: path to write the transformed DataFrame
    """
    import os

    df = read_data(input_dir)
    df.drop(['fuss_in', 'fuss_out'], axis=1, inplace=True)
    df.dropna(inplace=True)
    df.columns = [
        'counting_station', 'datetime', 'velo_in', 'velo_out', 'objectid'
    ]

    if output_path:
        # create the output directory if it does not exist
        output_folder = os.path.dirname(output_path)
        os.makedirs(output_folder, exist_ok=True)
        # write the DataFrame
        df.reset_index(drop=True).to_parquet(output_path, compression='gzip')

    return df


if __name__=='__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Process ZH Bikes data')

    parser.add_argument('input',  help='Input directory.')
    parser.add_argument('output', help='Output file.')
    args = parser.parse_args()
    clean_data(args.input, args.output)
