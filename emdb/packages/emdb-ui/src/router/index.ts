import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import MovieView from '@/views/MovieView.vue'
import MovieEditView from '@/views/MovieEditView.vue'

import keycloak from '@/auth/keycloak'

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
  ],
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    if (!keycloak.authenticated) {
      keycloak.login({ redirectUri: window.location.origin + to.fullPath });
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
