import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import IngestView from '@/views/IngestView.vue'
import MovieView from '@/views/MovieView.vue'
import MovieEditView from '@/views/MovieEditView.vue'
import PersonView from '@/views/PersonView.vue'
import PersonEditView from '@/views/PersonEditView.vue'
import SeriesView from '@/views/SeriesView.vue'
import SeriesEditView from '@/views/SeriesEditView.vue'

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
    {
      path: '/movie/:id/edit',
      name: 'MovieEdit',
      component: MovieEditView,
    },    
    {
      path: '/person/:id',
      name: 'Person',
      component: PersonView,
    },
    {
      path: '/person/:id/edit',
      name: 'PersonEdit',
      component: PersonEditView,
    },    
    {
      path: '/series/:id',
      name: 'Series',
      component: SeriesView,
    },
    {
      path: '/series/:id/edit',
      name: 'SeriesEdit',
      component: SeriesEditView,
    },                 
  ],
})

export default router
