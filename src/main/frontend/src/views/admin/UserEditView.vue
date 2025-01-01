<script setup lang="ts">
	import { ref } from 'vue';
	import { useRoute } from 'vue-router';

	const id = ref(useRoute().params.id);
</script>

<template>
	<main>
		<h1>User {{ id }}</h1>
		<h2>TODO:</h2>
	</main>
</template>
