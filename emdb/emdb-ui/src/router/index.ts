import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/views/HomeView.vue'
import MovieView from '@/views/MovieView.vue'
import PersonView from '@/views/PersonView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView,
    },   
    {
      path: '/movie/:id',
      name: 'Movie',
      component: MovieView,
    },
    {
      path: '/person/:id',
      name: 'Person',
      component: PersonView,
    },
  ],
})

export default router
