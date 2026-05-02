import { createRouter, createWebHistory } from 'vue-router'

import keycloak from '@/auth/keycloak'
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
      path: '/movie/:id',
      name: 'Movie',
      component: MovieView,
    },
    {
      path: '/movie/:id/edit',
      name: 'MovieEdit',
      component: MovieEditView,
      meta: {
        requiresAuth: true, 
        requiredRole: 'admin' 
      }
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
      meta: {
        requiresAuth: true, 
        requiredRole: 'admin' 
      }
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
      meta: {
        requiresAuth: true, 
        requiredRole: 'admin' 
      }
    },
    {
      path: '/ingest',
      name: 'Ingest',
      component: IngestView,
      meta: {
        requiresAuth: true, 
        requiredRole: 'admin' 
      }
    },    
  ],
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    if (!keycloak.authenticated) {
      const base = import.meta.env.BASE_URL.replace(/\/$/, ''); // '/emdb'
      keycloak.login({
        redirectUri: window.location.origin + base + to.path
      });
      return;
    }

    if (to.meta.requiredRole) {
      const hasRole = keycloak.hasRealmRole(to.meta.requiredRole as string);
      if (!hasRole) {
        console.warn('Access denied: Missing required role.');
        next({ name: 'Home' });
        return;
      }
    }
  }
  next();
})

export default router
