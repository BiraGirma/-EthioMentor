// Can add subtle floating cards, interactive hover effects
document.querySelectorAll('.feature-card').forEach(card => {
    card.addEventListener('mouseenter', () => card.classList.add('animate-pop'));
    card.addEventListener('mouseleave', () => card.classList.remove('animate-pop'));
});
