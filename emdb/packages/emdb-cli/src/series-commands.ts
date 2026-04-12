import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { useErrorHandler } from './services/useErrorHandler.js';
import { type IngestMedia, IngestSource, MediaType } from '@emdb/common';
import { SaveSeriesSchema } from './schemas/SaveSeriesSchema.js';
import { UpdateSeriesSchema } from './schemas/UpdateSeriesSchema.js';

export { seriesCommand };

const { handleError } = useErrorHandler();

const seriesCommand = new Command('series')
  .description('Perform CRUD operations on a series');

seriesCommand
  .command('ingest')
  .description('Ingest a series from TMDB')
  .argument('<tmdbId>', 'The TMDB id of the series to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli series ingest 4614
`)  
  .action(ingest);

seriesCommand
  .command('save')
  .description('Save a series from TMDB')
  .argument('<file>', 'The file containing the series to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli series save The-Simpsons.json
`)  
  .action(save);

seriesCommand
  .command('find')
  .description('Find a series in EMDB')
  .argument('<id>', 'The id of the series to find')
  .addHelpText('after', `
Examples:
  $ emdb-cli series find 1
`)  
  .action(find);

seriesCommand
  .command('find-all')
  .description('Find all the series in EMDB')
  .addHelpText('after', `
Examples:
  $ emdb-cli series find-all
`)  
  .action(findAll);

seriesCommand
  .command('update')
  .description('Update a series from TMDB')
  .argument('<id>', 'The id of the series to update')
  .argument('<file>', 'The file containing the series update')
  .addHelpText('after', `
Examples:
  $ emdb-cli series update 1 The-Simpsons.json
`)  
  .action(update);

seriesCommand
  .command('delete')
  .description('Delete a series in EMDB')
  .argument('<id>', 'The id of the series to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli series delete 1
`)  
  .action(deleteById);

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command: IngestMedia = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.SERIES
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  } catch (error: any) {
    handleError(error);
  }
};

async function save(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = SaveSeriesSchema.safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { saveSeries } = useEmdb();
    const start = performance.now();
    const { status, series } = await saveSeries(parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });

    if (status === 201) {
      console.log(`Created series: ${series?.title} in ${et} ms.`);
      console.log(series);
    } else if (status === 200) {
      console.log(`Updated series: ${series?.title} in ${et} ms.`);
      console.log(series);
    } else if (status === 204) {
      console.log(`No changes needed for ${parsed.data.title}. Skipped in ${et} ms.`);
    } else {
      console.log(`Saved with status ${status} in ${et} ms.`);
    }
  } catch (error: any) {
    handleError(error);
  }
};

async function find(id: number) {
  try {
    const { findSeries } = useEmdb();
    const start = performance.now();
    const series = await findSeries(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found ${series.title} in: ${et} ms.`);    
    console.log(series);
  } catch (error: any) {
    handleError(error);
  }
};

async function findAll() {
  try {
    const { findAllSeries } = useEmdb();
    const start = performance.now();
    const series = await findAllSeries();
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Found all series in: ${et} ms.`);    
    console.log(series);
  } catch (error: any) {
    handleError(error);
  }
};

async function update(id: number, fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const parsed = UpdateSeriesSchema.safeParse(JSON.parse(file));
    if (!parsed.success) {
      console.error(`Validation failed for file: ${fileName}`);
      console.error(JSON.stringify(parsed.error.format(), null, 2));
      process.exit(1);
    }
    const { updateSeries } = useEmdb();
    const start = performance.now();
    const { status, series } = await updateSeries(id, parsed.data);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });

    if (status === 201) {
      console.log(`Created series: ${series?.title} in ${et} ms.`);
      console.log(series);
    } else if (status === 200) {
      console.log(`Updated series: ${series?.title} in ${et} ms.`);
      console.log(series);
    } else if (status === 204) {
      console.log(`No changes needed for ${parsed.data.title}. Skipped in ${et} ms.`);
    } else {
      console.log(`Updated with status ${status} in ${et} ms.`);
    }
  } catch (error: any) {
    handleError(error);
  }
};

async function deleteById(id: number) {
  try {
    const { deleteSeries } = useEmdb();
    const start = performance.now();
    await deleteSeries(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted series ${id} in: ${et} ms.`);    
  } catch (error: any) {
    handleError(error);
  }
};