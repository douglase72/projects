#!/usr/bin/env node

import { Command } from 'commander';
import { fileURLToPath } from 'url';
import path from 'path';
import dotenv from 'dotenv';

import { movieCommand } from './movie-commands.js';

process.env.DOTENV_CONFIG_QUIET = 'true';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
dotenv.config({ path: path.resolve(__dirname, '..', '.env') });

const program = new Command();
program
  .name('cli')
  .description('An EMDB command line interface')
  .version('1.0.0');
program.addCommand(movieCommand);
program.parse(process.argv);