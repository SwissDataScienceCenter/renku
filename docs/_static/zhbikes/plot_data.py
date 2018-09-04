# -*- coding: utf-8 -*-
#
# A script to generate some plots from the open bike counting data from
# Zürich, Switzerland
#

import os

import matplotlib
matplotlib.use('Agg')
import matplotlib.pylab as plt
import pandas as pd



def sum_by_week(df, station):
    """Helper function to aggregate data by week per station."""
    df_station = df[df['counting_station'] == station]
    aggregated = df_station.set_index('datetime').resample('1W').agg({
        'velo_in':
        'sum',
        'velo_out':
        'sum'
    }).reset_index()
    aggregated['velo_all'] = aggregated['velo_out'] + aggregated['velo_in']
    aggregated['velo_out'] = -aggregated['velo_out']
    aggregated['counting_station'] = station
    return aggregated


def convert_to_weekly(df, output_path=None):
    """
    Convert the DataFrame to weekly values.

    Args:

    df: dataframe of daily bike counts

    output_path (optional): path to write out the restructed DataFrame
    """
    stations = df['counting_station'].unique()
    dataframes_weekly = [sum_by_week(df, station) for station in stations]
    df_weekly = pd.concat(dataframes_weekly).reset_index(drop=True)

    if output_path:
        # create the output directory if it does not exist
        output_folder = os.path.dirname(output_path)
        os.makedirs(output_folder, exist_ok=True)
        # write the DataFrame
        df.reset_index(drop=True).to_feather(output_path)

    return df_weekly


def remove_if_exists(path):
    """Helper function to remove a path if it exists."""
    try:
        os.remove(path)
    except OSError:
        pass


def generate_plots(preprocessed_input):
    """
    Create some visualizations of the data.

    Args:

    preprocessed_input: path where of the output from clean_data
    """
    import os

    import seaborn as sns

    df = pd.read_parquet(preprocessed_input)
    df['datetime'] = df['datetime'].apply(pd.Timestamp)
    df_weekly = convert_to_weekly(df)

    sns.set(font_scale=2)
    grid = sns.FacetGrid(
        df_weekly,
        col="counting_station",
        hue="counting_station",
        palette="tab20c",
        col_wrap=4,
        height=3,
        aspect=3)
    grid.map(plt.plot, "datetime", "velo_in")
    grid.map(plt.plot, "datetime", "velo_out")
    grid.map(plt.axhline, y=0, ls=":", c=".5")
    grid.set_xticklabels(rotation=30)

    # check if the figures exist already. If so, remove them
    remove_if_exists('figs/grid_plot.png')
    plt.savefig('figs/grid_plot.png')

    f = plt.figure(figsize=(15,10))
    sns.set(rc={'figure.figsize': (15, 10)})
    sns.lineplot(
        x="datetime", y="velo_all", hue="counting_station", data=df_weekly)
    remove_if_exists('figs/cumulative.png')
    plt.savefig('figs/cumulative.png')


if __name__ == '__main__':
    import argparse

    # process command-line inputs
    parser = argparse.ArgumentParser(description='Process ZH Bikes data')
    parser.add_argument('input_path', help='Pre-processed input file.')
    args = parser.parse_args()

    # make the plots
    os.makedirs('figs', exist_ok=True)
    generate_plots(args.input_path)
