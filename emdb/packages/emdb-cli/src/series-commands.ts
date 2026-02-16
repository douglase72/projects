import { Command } from 'commander';
import { promises as fs } from 'fs';

import { useEmdb } from './services/useEmdb.js';
import { 
  IngestSource, 
  MediaType, 
  SchedulerType, 
  type ExecuteScheduler } from '@emdb/common';

export { seriesCommand };

const seriesCommand = new Command('series')
  .description('Perform CRUD operations on a series');

seriesCommand
  .command('delete')
  .description('Delete a series in EMDB')
  .argument('<id>', 'The id of the series to delete')
  .addHelpText('after', `
Examples:
  $ emdb-cli series delete 1
`)  
  .action(deleteById);
  
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
  .command('ingest')
  .description('Ingest a series from TMDB into EMDB')
  .argument('<tmdbId>', 'The TMDB id of the series to ingest')
  .addHelpText('after', `
Examples:
  $ emdb-cli series ingest 1396
`)  
  .action(ingest);

seriesCommand
  .command('save')
  .description('Save a series from TMDB')
  .argument('<file>', 'The file containing the series to save')
  .addHelpText('after', `
Examples:
  $ emdb-cli series save The-Simpsons-20260205-202941.json
`)  
  .action(save);  

seriesCommand
  .command('scheduler')
  .description('Execute the series scheduler')
  .addHelpText('after', `
Examples:
  $ emdb-cli series scheduler
`)  
  .action(scheduler);  

seriesCommand
  .command('update')
  .description('Update a series from TMDB')
  .argument('<id>', 'The id of the series to update')
  .argument('<file>', 'The file containing the series to update')
  .addHelpText('after', `
Examples:
  $ emdb-cli series update The-Simpsons-20260205-202941.json
`)  
  .action(update);  

async function deleteById(id: number) {
  try {
    const { deleteSeries } = useEmdb();
    const start = performance.now();
    await deleteSeries(id);
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Deleted ${id} in: ${et} ms.`);  
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
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
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function ingest(tmdbId: number) {
  try {
    const { ingest } = useEmdb();
    const command = {
      tmdbId: tmdbId,
      source: IngestSource.CLI,
      type: MediaType.SERIES
    };
    const jobId = await ingest(command);
    console.log(`Job Id: ${jobId}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function save(fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const { saveSeries } = useEmdb();
    const start = performance.now();
    const series = await saveSeries(JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Saved ${series.title} in: ${et} ms.`);    
    console.log(series);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function scheduler() {
  try {
    const { executeScheduler } = useEmdb();
    const command: ExecuteScheduler = { type: SchedulerType.SERIES };
    const status = await executeScheduler(command);
    console.log(`Status: ${status}`);
  }  catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};

async function update(id:number, fileName: string) {
  try {
    const file = await fs.readFile(fileName, 'utf-8');
    const { updateSeries } = useEmdb();
    const start = performance.now();
    const series = await updateSeries(id, JSON.parse(file));
    const et = (performance.now() - start).toLocaleString(undefined, {
      minimumFractionDigits: 1, maximumFractionDigits: 1
    });
    console.log(`Updated ${series.title} in: ${et} ms.`);       
    console.log(series);
  } catch (error: any) {
    console.dir(error, { depth: null }); 
    process.exit(1);
  }
};