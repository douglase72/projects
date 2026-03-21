import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { useErrorHandler } from './composables/useErrorHandler.js';
import { type IngestMedia, IngestSource, MediaType } from '@emdb/common';
import { SaveSeriesSchema } from './schemas/SaveSeriesSchema.js';

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