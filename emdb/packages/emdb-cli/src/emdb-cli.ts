#!/usr/bin/env node

import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import path from 'path';
import { Command } from 'commander';

process.env.DOTENV_CONFIG_QUIET = 'true';
const __dirname = path.dirname(fileURLToPath(import.meta.url));
const envPath = path.resolve(__dirname, '../.env');
dotenv.config({ path: envPath });

import { movieCommand } from './movie-commands.js';
import { personCommand } from './person-commands.js';
import { seriesCommand } from './series-commands.js';

const program = new Command();

program
  .name('cli')
  .description('An EMDB command line interface')
  .version('1.0.0');
program.addCommand(movieCommand);
program.addCommand(personCommand);
program.addCommand(seriesCommand);
program.parse(process.argv);