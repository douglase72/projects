import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/views/HomeView.vue'
import MovieView from '@/views/MovieView.vue'
import IngestView from '@/views/IngestView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView,
    },   
    {
      path: '/ingest',
      name: 'Ingest',
      component: IngestView,
    },    
    {
      path: '/movie/:id',
      name: 'Movie',
      component: MovieView,
    },
  ],
})

export default router
