import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

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
      component: () => import('@/views/MovieView.vue'),
    },
    {
      path: '/movie/:id/edit',
      name: 'MovieEdit',
      component: () => import('@/views/MovieEditView.vue'),
    },    
    {
      path: '/person/:id',
      name: 'Person',
      component: () => import('@/views/PersonView.vue'),
    },
    {
      path: '/person/:id/edit',
      name: 'PersonEdit',
      component: () => import('@/views/PersonEditView.vue'),
    },    
    {
      path: '/series/:id',
      name: 'Series',
      component: () => import('@/views/SeriesView.vue'),
    },             
  ],
})

export default router
