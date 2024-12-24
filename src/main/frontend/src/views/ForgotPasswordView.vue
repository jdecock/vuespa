<template>
	<main>
		<h1>Forgot Password</h1>

		<p>Enter the address you used when you joined and we'll send you instructions to reset your password.</p>

		<p>
			For security reasons, we <em>do not</em> store your password. So rest assured that we will never send you your
			password via email.
		</p>

		<form>
			<div>
				<label for="inputEmail">Email</label><br />
				<input type="email" id="inputEmail" />
			</div>

			<div>
				<button type="submit">Send Reset Instructions</button>
			</div>
		</form>
	</main>
</template>
