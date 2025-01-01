import type { Alert } from '@/types/alert.ts';
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { AlertType } from '@/types/alertType.ts';

export const useAlertStore = defineStore('alertStore', () => {
	const alerts = ref<Alert | null>(null);

	function success(message?: string) {
		if (message) {
			alerts.value = { message: message, type: AlertType.Success };
		}
	}

	function info(message?: string) {
		if (message) {
			alerts.value = { message: message, type: AlertType.Info };
		}
	}

	function error(message?: string) {
		if (message) {
			alerts.value = { message: message, type: AlertType.Error };
		}
	}

	function clear() {
		alerts.value = null;
	}

	return {
		success,
		info,
		error,
		clear
	};
});
