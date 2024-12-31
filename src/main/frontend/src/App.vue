<script setup lang="ts">
	import { RouterLink, RouterView } from 'vue-router';
</script>

<template>
	<header>
		<nav>
			<ul>
				<li><RouterLink to="/">Home</RouterLink></li>
				<li><RouterLink to="/sign-in">Sign In</RouterLink></li>
				<!--
				<li><RouterLink to="/sign-out">Sign Out</RouterLink></li>
				-->
				<li><RouterLink to="/account">My Account</RouterLink></li>
				<li><RouterLink to="/admin/users">Search Users</RouterLink></li>
			</ul>
		</nav>
	</header>

	<RouterView />
</template>

<style scoped>
	header {
		line-height: 1.5;
	}

	nav {
		width: 100%;
		font-size: 12px;
		text-align: center;
		margin-top: 2rem;
	}

	nav ul {
		margin: 0;
		padding: 0;
		text-align: right;
	}

	nav li {
		display: inline-block;
		list-style: none;
	}

	nav a.router-link-exact-active {
		color: var(--color-text);
	}

	nav a.router-link-exact-active:hover {
		background-color: transparent;
	}

	nav a {
		display: inline-block;
		padding: 0 1rem;
		border-left: 1px solid var(--color-border);
	}

	nav a:first-of-type {
		border: 0;
	}

	@media (min-width: 1024px) {
		nav {
			font-size: 1rem;
		}
	}
</style>
