use std::sync::atomic::AtomicU64;

use std::sync::{LazyLock, atomic::Ordering};
use std::time::Instant;

#[derive(Debug, Default)]
pub struct Activity {
    pub last_input_ms: AtomicU64,
    pub last_fwd_ms: AtomicU64,
    pub bytes_out: AtomicU64,
}

static START: LazyLock<Instant> = LazyLock::new(Instant::now);

fn now_ms() -> u64 {
    START.elapsed().as_millis() as u64
}

impl Activity {
    pub fn track_input(&self) {
        self.last_input_ms.fetch_max(now_ms(), Ordering::Relaxed);
    }

    pub fn track_forward(&self, nbytes: u64) {
        self.last_fwd_ms.fetch_max(now_ms(), Ordering::Relaxed);
        self.bytes_out.fetch_add(nbytes, Ordering::Relaxed);
    }
}

impl std::fmt::Display for Activity {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let now = now_ms();
        write!(
            f,
            "Activity( last_input: {}ms ago, last_fmd: {}ms ago )",
            now - self.last_input_ms.load(Ordering::Relaxed),
            now - self.last_fwd_ms.load(Ordering::Relaxed)
        )
    }
}
